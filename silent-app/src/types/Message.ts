export type Message = {
  id: number;
  subject: string;
  content: string;
  numberToTrigger: number;
  typeToTrigger: 'DAYS' | 'HOURS' | 'MINUTES';
  recipients: string[];
  active: boolean;
  lastCheckIn?: string;
  nextReminder?: string;
};
